package pio.daw;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Library implements Controlable {
    private Map<String, User> users;

    public static Library fromFile(Path path) {
        Library library = new Library();
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                
                if (line.trim().isEmpty()) return;
                String[] parts = line.split(";");
                if (parts.length != 2) return;
                String id = parts[0].trim();
                String eventStr = parts[1].trim().toUpperCase();
                EventType event = null;
                if (eventStr.equals("ENTRADA")) {
                    event = EventType.ENTRY;
                } else if (eventStr.equals("SALIDA")) {
                    event = EventType.EXIT;
                }
                if (event != null) {
                    library.registerChange(id, event);
                }
            });
        } catch (IOException e) {
            System.err.println("Error leyendo fichero: " + e.getMessage());
            System.exit(1);
        }
        return library;
    }

    private Library() {
        this.users = new HashMap<>();
    }

    @Override
    public void registerChange(String id, EventType e) {
        User user = users.get(id);
        if (user == null) {
            user = new User(id);
            users.put(id, user);
        }
        user.registerEvent(e);
    }

    @Override
    public List<User> getCurrentInside() {
        return users.values().stream()
                .filter(User::isInside)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMaxEntryUsers() {
        int max = users.values().stream()
                .mapToInt(User::getEntryCount)
                .max().orElse(0);
        return users.values().stream()
                .filter(u -> u.getEntryCount() == max)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getUserList() {
        
        return users.values().stream()
                .filter(u -> u.getEntryCount() > 0)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public void printResume() {
        System.out.println("Usuarios actualmente dentro de la biblioteca:");
        getCurrentInside().forEach(u -> System.out.println(u.getId()));
        System.out.println();

        System.out.println("Número de entradas por usuario:");
        getUserList().forEach(u -> System.out.println(u.getId() + " -> " + u.getEntryCount()));
        System.out.println();

        System.out.println("Usuario(s) con más entradas:");
        getMaxEntryUsers().forEach(u -> System.out.println(u.getId()));
    }
}