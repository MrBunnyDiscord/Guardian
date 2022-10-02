package de.mrbunny.guardian.util.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InstallationResponse {

    public int id;
    public int app_id;

    public int target_id;
    public String target_type;

    public boolean isOrganization(){
        return "Organization".equals (target_type);
    }

    public String[] events;

    @Override
    public String toString() {
        return "InstalllatinResponse [id=" + id + ", app_id=" + app_id + ", target_id=" + target_id + ", target_type=" + target_type + ", events=" + Arrays.toString (events) + "]";
    }
}
