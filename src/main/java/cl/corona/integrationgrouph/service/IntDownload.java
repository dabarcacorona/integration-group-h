package cl.corona.integrationgrouph.service;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Vector;

@Service
public class IntDownload {

    @Value("${sftpo.ip}")
    private String sftpip;

    @Value("${sftpo.prt}")
    private int sftpprt;

    @Value("${sftpo.usr}")
    private String sftpusr;

    @Value("${sftpo.pss}")
    private String sftppss;

    @Value("${sftpo.org}")
    private String sftporg;

    @Value("${sftpo.dst}")
    private String sftpdtn;

    @Value("${flag_SDITRFDTE}")
    private String v_SDITRFDTE;

    @Value("${flag_CHLPRCE2}")
    private String v_CHLPRCE2;

    @Value("${flag_MTCSDIRPLORD}")
    private String v_MTCSDIRPLORD;

    @Value("${flag_SDIRTVDTE}")
    private String v_SDIRTVDTE;

    @Value("${flag_SDICALCTL}")
    private String v_SDICALCTL;

    @Value("${separador.carpetas}")
    private String separador;

    @Value("${largo.archivo}")
    private int largo_archivo;

    private static final Logger LOG = LoggerFactory.getLogger(IntDownloadInvSelectivo.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    String strDir = System.getProperty("user.dir");


    public void DownloadFile() throws IOException {

        JSch jsch = new JSch();
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "password");
        jsch.setConfig(config);

        try {

            Session session = jsch.getSession(sftpusr, sftpip, sftpprt);
            session.setConfig("PreferredAuthentications", "password");
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(sftppss);
            session.connect();
            Channel channel = session.openChannel("sftp");
            ChannelSftp sftp = (ChannelSftp) channel;
            sftp.connect();

            final String path = strDir + separador + sftpdtn + separador;
            //final String path = sftpdtn;
            //LOG.info(path);

            Vector<ChannelSftp.LsEntry> entries = sftp.ls(sftporg);

            //download all files (except the ., .. and folders) from given folder
            for (ChannelSftp.LsEntry en : entries) {
                if (en.getFilename().equals(".") || en.getFilename().equals("..") || en.getAttrs().isDir()) {
                    continue;
                }

                String filename = StringUtils.getFilename(en.getFilename());
                //String sSubCadena = filename.substring(0, largo_archivo).toUpperCase();
                int end = filename.indexOf("_");
                String sSubCadena = filename.substring(0, end).toUpperCase();

                if ((sSubCadena.equals("SDITRFDTE")) || sSubCadena.equals("CHLPRCE2")
                        || sSubCadena.equals("MTCSDIRPLORD") || sSubCadena.equals("SDIRTVDTE") || sSubCadena.equals("SDICALCTL")) {

                        LOG.info("Downloading " + (sftporg + en.getFilename()) + " ---> " + path + en.getFilename());
                        sftp.get(sftporg + en.getFilename(), path + en.getFilename());
                        sftp.rm(sftporg + en.getFilename());
                        LOG.info("{} : Download Ok", dateTimeFormatter.format(LocalDateTime.now()));

                }
            }

            sftp.exit();
            channel.disconnect();
            session.disconnect();

        } catch (JSchException e) {
            LOG.error("No se pudo realizar la conexión ,{}",  e);
        } catch (SftpException e) {
            e.printStackTrace();
        }

    }
}
