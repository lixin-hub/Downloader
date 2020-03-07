package com.example.download;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import android.os.Environment;
import java.io.RandomAccessFile;
import java.io.InputStream;
import java.util.Random;
import android.widget.TextView;
import android.content.Context;

public class DownloadUtil
{	

	MainActivity context;
	StringBuilder builder = new StringBuilder();;
	TextView text;
	long length;
	long block;
	int threadNum;

	public DownloadUtil(MainActivity context)
	{
		this.context = context;
	}

    public void download(String path, int threadNum, TextView text) throws MalformedURLException, IOException
    {
		this.text = text;
		this.threadNum = threadNum;

		for (int threadId=0;threadId < threadNum;threadId++)
		{
			new DownloadThread(path, threadId).start();
			builder.append("threadId=" + threadId + "启动" + "\n");
			text.setText(builder.toString());
		}

	}



    private static String getFileName(String path)
    {
        return    path.substring(path.lastIndexOf("/")); 
    }

    private class DownloadThread extends Thread
    {

        String path;
        File file;
        int threadId;

        public DownloadThread(String path, int threadId)
        {

            this.path = path;
            this.threadId = threadId;

        }

        public void run() 
        {


            try
            {
                URL url=new URL(path);
                try
                {
                    HttpsURLConnection conn=(HttpsURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
					length = getContentLength(path);
					int block=length%threadNum==0 ? (int)length/threadNum : (int)length/threadNum+1;
					int start=threadId * block;
					int end=(threadId + 1) * block;
					builder.append("threadId=" + threadId + "\n" + "block=" + block + "start=" + start + "end=" + end + "\n");
					context.runOnUiThread(new Runnable(){

							@Override
							public void run()
							{
								text.setText(builder.toString());
							}
						});


					conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
					String directory=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
					file = new File(directory + getFileName(path));
					if (file.exists())
					{file.delete();}
					else
					{file.createNewFile();}
					RandomAccessFile randAccessFile=new RandomAccessFile(file, "rw"); 
					randAccessFile.setLength(length);
                    randAccessFile.seek(start);
                    InputStream is=conn.getInputStream();
                    byte[] b=new byte[1024];
                    int len=0;
                    while ((len = is.read(b)) != -1)
                    {
                        randAccessFile.write(b, 0, len);
                    }
                    randAccessFile.close();
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
					builder.append(e.toString());
					text.setText(builder.toString());
                }

            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
				builder.append(e.toString());
				text.setText(builder.toString());
            }

			
            super.run();
        }


		public long getContentLength(String path)
		{
			try
			{
				URL url=new URL(path);
				HttpsURLConnection conn=(HttpsURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				int length=conn.getContentLength();
				if (length != 0)
				{
					return length;
				}
			}
			catch (Exception e)
			{

				e.printStackTrace();
			}
			return 0;
		}

    }




}
