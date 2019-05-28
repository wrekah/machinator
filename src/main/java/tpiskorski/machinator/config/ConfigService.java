package tpiskorski.machinator.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public abstract class ConfigService {

    private final List<PropertyChangeListener> listeners = new ArrayList<>();
    protected Config config;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    protected void firePropertyChange(String property, Object oldValue, Object newValue) {
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
        }
    }

    protected abstract Config loadConfig();
    public abstract void modifyConfig(Config newConfig);

    public void reload() {
        config = loadConfig();
    }

    public synchronized Config getConfig() {
        config = loadConfig();
        return config;
    }
}
