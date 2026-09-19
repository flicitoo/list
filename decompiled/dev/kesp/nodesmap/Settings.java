/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 */
package dev.kesp.nodesmap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.kesp.nodesmap.Rules;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;

public final class Settings {
    public String worldFile = "world.json";
    public String townsFile = "towns.json";
    public String worldUrl = "";
    public String townsUrl = "";
    public String server = "";
    public String dimension = "";
    public double baseSeconds = 300.0;
    public double edgeMultiplier = 1.0;
    public double homeMultiplier = 1.0;
    public double blocksPerSecond = 4.3;
    public boolean diagonal = false;
    public boolean resourceMultipliers = true;
    public boolean enabled = true;
    public boolean alerts = true;
    public boolean borders = true;
    public int opacity = 85;
    public int refreshMinutes = 10;
    public boolean routeGuide = true;
    public String whitelistUrl = "https://raw.githubusercontent.com/flicitoo/list/main/whitelist.json";
    public int whitelistRefreshMinutes = 5;

    public Rules rules() {
        return new Rules(this.baseSeconds, this.edgeMultiplier, this.homeMultiplier, this.blocksPerSecond, this.diagonal, this.resourceMultipliers);
    }

    public void validate() {
        this.rules();
        if (this.opacity < 0 || this.opacity > 255 || this.refreshMinutes < 1 || this.refreshMinutes > 1440) {
            throw new IllegalArgumentException("Opacidad 0\u2013255; intervalo 1\u20131440 minutos");
        }
        if (this.worldFile == null || this.townsFile == null || this.worldUrl == null || this.townsUrl == null || this.server == null) {
            throw new IllegalArgumentException("Configuraci\u00f3n incompleta");
        }
        if (this.worldFile.isBlank() || this.townsFile.isBlank()) {
            throw new IllegalArgumentException("Indica los dos archivos JSON");
        }
    }

    public static Settings load(Path path) throws IOException {
        if (!Files.exists(path, new LinkOption[0])) {
            return new Settings();
        }
        try (BufferedReader r = Files.newBufferedReader(path);){
            Settings s = (Settings)new Gson().fromJson((Reader)r, Settings.class);
            if (s == null) {
                throw new IOException("Configuraci\u00f3n vac\u00eda");
            }
            s.validate();
            Settings settings = s;
            return settings;
        }
    }

    public void save(Path path) throws IOException {
        this.validate();
        Files.createDirectories(path.getParent(), new FileAttribute[0]);
        Path tmp = path.resolveSibling(String.valueOf(path.getFileName()) + ".tmp");
        Files.writeString(tmp, (CharSequence)new GsonBuilder().setPrettyPrinting().create().toJson((Object)this), new OpenOption[0]);
        Settings.replace(tmp, path);
    }

    static void replace(Path from, Path to) throws IOException {
        try {
            Files.move(from, to, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (AtomicMoveNotSupportedException e) {
            Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

