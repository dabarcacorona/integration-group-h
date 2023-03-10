package cl.corona.integrationgrouph.service;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Service
public class IntUploadInvSelectivo {
    @Value("${sftpddev.ip}")
    private String d_sftpip;

    @Value("${sftpddev.prt}")
    private int d_sftpprt;

    @Value("${sftpddev.usr}")
    private String d_sftpusr;

    @Value("${sftpddev.pss}")
    private String d_sftppss;

    @Value("${sftpddev.org}")
    private String d_sftporg;

    @Value("${sftpddev.dst_IPHY_s}")
    private String d_sftpdtn_IPHY_s;

    @Value("${name.file}")
    private String d_namefile;

    @Value("${separador.carpetas}")
    private String separador;

    @Value("${largo.archivo}")
    private int largo_archivo;

    private static final Logger LOG = LoggerFactory.getLogger(IntUploadInvSelectivo.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    String strDir = System.getProperty("user.dir");

    public void UploadFile() throws IOException {

        JSch jsch = new JSch();
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "password");
        jsch.setConfig(config);

        try {

            Session session = jsch.getSession(d_sftpusr, d_sftpip, d_sftpprt);
            session.setConfig("PreferredAuthentications", "password");
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(d_sftppss);
            session.connect();
            Channel channel = session.openChannel("sftp");
            ChannelSftp d_sftp = (ChannelSftp) channel;
            d_sftp.connect();

            final String path = strDir + separador + d_sftporg;
            //final String path = sftporg;

            File directory = new File(path);
            File[] fList = directory.listFiles();

            for (File file : fList) {

                String name = StringUtils.getFilename(file.getName());
                int end = name.indexOf("_");
                String sSubCadena = name.substring(0, end).toUpperCase();

                //if (sSubCadena.equals(d_namefile)) {

                switch (sSubCadena) {
                    //FLUJO RSL
                    case "SDIPHYDTI":
                        if (file.isFile()) {
                            String filename = file.getAbsolutePath();
                            //System.out.println(filename + " transfered to --> " + sftpdtn);
                            LOG.info("Uploading IPHY " + filename + " ---> " + d_sftpdtn_IPHY_s);
                            d_sftp.put(filename, d_sftpdtn_IPHY_s);
                            file.delete();
                            LOG.info("{} : Upload Ok", dateTimeFormatter.format(LocalDateTime.now()));
                        }
                        break;
                }
            }

            d_sftp.exit();
            channel.disconnect();
            session.disconnect();

        } catch (JSchException e) {
            LOG.error("No se pudo realizar la conexión ,{}", e);
        } catch (SftpException e) {
            e.printStackTrace();
        }

    }
}
