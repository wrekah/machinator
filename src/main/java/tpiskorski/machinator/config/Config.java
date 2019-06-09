package tpiskorski.machinator.config;

public class Config {

    private int pollInterval;
    private String backupLocation;
    private boolean notifications;

    private Config(Builder builder) {
        this.pollInterval = builder.pollInterval;
        this.backupLocation = builder.backupLocation;
        this.notifications = builder.notifications;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Config createDefault() {
        return builder()
            .pollInterval(30)
            .backupLocation("/dev/null")
            .notifications(false)
            .build();
    }

    public static Config copy(Config old) {
        return builder()
            .pollInterval(old.getPollInterval())
            .backupLocation(old.getBackupLocation())
            .notifications(old.areNotificationsEnabled())
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

    public static class Builder {

        private int pollInterval;
        private String backupLocation;
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

        public Config build() {
            return new Config(this);
        }
    }
}
