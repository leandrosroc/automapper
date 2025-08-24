package com.automapper.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gerenciador de profiles de mapeamento
 */
public class ProfileManager {
    private static final Map<String, MappingProfile> profiles = new ConcurrentHashMap<>();
    
    /**
     * Registra um profile
     */
    public static void registerProfile(String name, MappingProfile profile) {
        profile.configure();
        profiles.put(name, profile);
    }
    
    /**
     * Obtém um profile registrado
     */
    public static MappingProfile getProfile(String name) {
        return profiles.get(name);
    }
    
    /**
     * Remove um profile
     */
    public static void removeProfile(String name) {
        profiles.remove(name);
    }
    
    /**
     * Lista todos os profiles registrados
     */
    public static Map<String, MappingProfile> getAllProfiles() {
        return profiles;
    }
    
    /**
     * Verifica se um profile existe
     */
    public static boolean hasProfile(String name) {
        return profiles.containsKey(name);
    }
}
