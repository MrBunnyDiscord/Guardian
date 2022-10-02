package de.mrbunny.guardian.util;

import de.mrbunny.guardian.util.json.InstallationResponse;
import org.kohsuke.github.GitHub;
import reactor.util.annotation.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GHInstallation {

    public static class NoSuchInstallationException extends Exception{
        private static final long serialVersionUID = -272376494673514859L;

        public NoSuchInstallationException(GHInstallation self, Exception cause){
            super("No installation found. " + (self.isOrganization() ? "Organization": "User") + ":" + self.owner + (self.isOrganization() ? "" : self.repo), cause);
        }

        public NoSuchInstallationException(String msg){
            super(msg);
        }
    }

    private static final Map<String, GHInstallation> cache = new HashMap<> ( );

    public static GHInstallation fromConfig() throws NoSuchInstallationException{
        String def = GithubUtil.defaultInstallation().orElseThrow(() -> new NoSuchInstallationException ("No default installation."));
        boolean isOrg = def.indexOf ('/') < 0;

        if(isOrg){
            return org(def);
        } else {
            String[] repo = def.split ("/");
            return repo(repo[0], repo[1]);
        }
    }

    public static synchronized GHInstallation org(String org) throws NoSuchInstallationException {
        GHInstallation ret = cache.get (org);
        if(ret == null){
            if(!cache.containsKey (org)){
                try {
                    ret = new GHInstallation (org);
                } catch (NoSuchInstallationException e){
                    cache.put (org, null);
                    throw e;
                }
                cache.put (org, ret);
            } else {
                throw new NoSuchInstallationException ("No such installation (cached).");
            }
        }
        return ret;
    }

    public static synchronized GHInstallation repo(String user, String repo) throws NoSuchInstallationException {
        String key = user + "/" + repo;
        GHInstallation ret = cache.get(key);
        if (ret == null) {
            if (!cache.containsKey(key)) {
                try {
                    ret = new GHInstallation(user, repo);
                } catch (NoSuchInstallationException e) {
                    cache.put(key, null);
                    throw e;
                }
                cache.put(key, ret);
            } else {
                throw new NoSuchInstallationException("No such installation (cached).");
            }
        }
        return ret;
    }

    private final String owner;
    private final @Nullable String repo;
    private final InstallationResponse installation;
    private @Nullable GitHub client;

    GHInstallation(String org) throws NoSuchInstallationException{
        this(org, null);
    }

    GHInstallation(String owner, @Nullable String repo) throws NoSuchInstallationException {
        this.owner = owner;
        this.repo = repo;
        try {
            this.installation = getInstallation();
        } catch (IOException e) {
            throw new NoSuchInstallationException(this, e);
        }
    }

    boolean isOrganization(){
        return repo == null;
    }

    private InstallationResponse getInstallation() throws IOException{
        if(isOrganization ()){
            return GithubUtil.getInstallation(owner);
        } else {
            return GithubUtil.getInstallation(owner, repo);
        }
    }

    public GitHub getClient(){
        if(client == null){
            client = GithubUtil.getClient(installation.id);
        }
        return client;
    }

    public String getOwner() {
        return owner;
    }
}
