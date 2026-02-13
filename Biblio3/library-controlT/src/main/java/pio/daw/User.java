package pio.daw;

public class User implements Localizable {
    private String id;
    private EventType lastEvent = null;
    private boolean inside = false;      // primitivo, evita problemas de desempaquetado
    private int entryCount = 0;

    public User(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public Boolean isInside() {
        return inside;   // autoboxing boolean
    }

    public void registerEvent(EventType e) {
        if (e == EventType.ENTRY) {
            if (!inside) {
                inside = true;
                entryCount++;
            }
            // entrada duplicada: se ignora
        } else if (e == EventType.EXIT) {
            if (inside) {
                inside = false;
            }
            // salida sin entrada
        }
        lastEvent = e;
    }

    public int getEntryCount() {
        return entryCount;
    }
}