package com.nttdata.filemonitor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to File Monitoring.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private String cron;

    private Folder folder;

    public Folder getFolder() {
        return folder;
    }

    public ApplicationProperties setFolder(Folder folder) {
        this.folder = folder;
        return this;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    static class Folder {
        private String location;

        public String getLocation() {
            return location;
        }

        public Folder setLocation(String location) {
            this.location = location;
            return this;
        }
    }
}
