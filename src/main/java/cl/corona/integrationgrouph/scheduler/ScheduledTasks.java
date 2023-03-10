package cl.corona.integrationgrouph.scheduler;

import cl.corona.integrationgrouph.service.*;
import cl.corona.integrationgrouph.setService.setDownloadIPHY;
import cl.corona.integrationgrouph.setService.setUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ScheduledTasks {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    private IntDownloadInvSelectivo intdownloadSelectivo;

    @Autowired
    private IntUploadInvSelectivo intuploadSelectivo;


    @Autowired
    private IntDownloadInvGeneral intdownloadGeneral;

    @Autowired
    private IntUploadInvGeneral intuploadGeneral;

    @Autowired
    private IntDownload intdownload;

    @Autowired
    private IntUpload intupload;

    @Autowired
    private setDownloadIPHY setdownloadIPHY;

    @Autowired
    private setUpload setupload;



    @Scheduled(cron = "${cron.expression}")
    public void scheduledJob() throws InterruptedException, IOException {

        LOG.info("{} : Inicio transferencia de archivos",
                dateTimeFormatter.format(LocalDateTime.now()));

        intdownloadSelectivo.DownloadFile();
        intuploadSelectivo.UploadFile();

        intdownloadGeneral.DownloadFile();
        intuploadGeneral.UploadFile();

        intdownload.DownloadFile();
        intupload.UploadFile();

        setdownloadIPHY.DownloadFile();
        setupload.UploadFile();

        LOG.info("{} : Fin transferencia de archivos",
                dateTimeFormatter.format(LocalDateTime.now()));

    }
}

