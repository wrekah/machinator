package tpiskorski.machinator.config;

public class Config {

    private int pollInterval;
    private String backupLocation;
    private String sshUser;
    private String sshPassword;
    private boolean notifications;

    private Config(Builder builder) {
        this.pollInterval = builder.pollInterval;
        this.backupLocation = builder.backupLocation;
        this.sshUser = builder.sshUser;
        this.sshPassword = builder.sshPassword;
        this.notifications = builder.notifications;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Config createDefault() {
        return builder()
            .pollInterval(30)
            .backupLocation("/dev/null")
            .sshUser("root")
            .sshPassword("root")
            .notifications(false)
            .build();
    }

    public boolean areNotificationsEnabled() {
        return notifications;
    }

    public int getPollInterval() {
        return pollInterval;
    }

    public String getBackupLocation() {
        return backupLocation;
    }

    public String getSshUser() {
        return sshUser;
    }

    public String getSshPassword() {
        return sshPassword;
    }

    public static class Builder {

        private int pollInterval;
        private String backupLocation;
        private String sshUser;
        private String sshPassword;
        private boolean notifications;

        public Builder pollInterval(int pollInterval) {
            this.pollInterval = pollInterval;
            return this;
        }

        public Builder notifications(boolean notifications) {
            this.notifications = notifications;
            return this;
        }

        public Builder backupLocation(String backupLocation) {
            this.backupLocation = backupLocation;
            return this;
        }

        public Builder sshUser(String sshUser) {
            this.sshUser = sshUser;
            return this;
        }

        public Builder sshPassword(String sshPassword) {
            this.sshPassword = sshPassword;
            return this;
        }

        public Config build() {
            return new Config(this);
        }
    }
}
