package com.phoenix.AwsFileUpload.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.sql.Blob;

@WebServlet("/upload")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1024 * 1024,
        maxFileSize = 1024 * 1024 * 1024 * 1024L,
        maxRequestSize = 1024 * 1024 * 1024 * 1024L
)

public class FileUpload extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Part f = req.getPart("file");
        String id = req.getParameter("index");
        String partNo = req.getParameter("totalChunks");
        String fileName = req.getParameter("fileName");
        String folderName = fileName.replace('.', 'V');


        File f1 = new File(getServletContext().getRealPath("") + "/" + folderName);
        if (!f1.exists()) {
            f1.mkdir();
        }


        File file = new File(getServletContext().getRealPath("") + "/" + folderName + "/" + id);

        System.out.println(id + "/" + partNo + " " + folderName + " " + f.getSize());

        InputStream is = f.getInputStream();

        FileOutputStream fos = new FileOutputStream(file);

        ReadableByteChannel rbc = Channels.newChannel(is);
        FileChannel fc = fos.getChannel();
        fc.transferFrom(rbc, 0, Long.MAX_VALUE);


        is.close();
        fos.close();
        fc.close();
        rbc.close();

        boolean AppendSucess = false;


        if (f1.listFiles().length == Integer.parseInt(partNo)) {
            System.out.println("Apending...." + f1.listFiles().length);
            File finalFile = new File(getServletContext().getRealPath("") + "/" + folderName + "/" + fileName);

            FileOutputStream fos2 = new FileOutputStream(finalFile);
            for (int i = 1; i <= Integer.parseInt(partNo); i++) {
                File chunck = new File(getServletContext().getRealPath("") + "/" + folderName + "/" + i);
                InputStream is2 = new FileInputStream(chunck);
                fos2.write(is2.readAllBytes());
                is2.close();
                if(i==Integer.parseInt(partNo)){
                    System.out.println("Deleting...");
                    for (int j = 1; j <= Integer.parseInt(id); j++) {
                        File chunckDel = new File(getServletContext().getRealPath("") + "/" + folderName + "/" + j);
                        chunckDel.delete();
                        chunck.delete();
                    }
                }
            }







        }


    }


}
