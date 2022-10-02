package de.mrbunny.guardian.util;

import de.mrbunny.guardian.StartGuardian;
import de.mrbunny.guardian.config.PermissionEntry;
import discord4j.core.object.entity.Member;
import discord4j.core.object.util.Snowflake;
import reactor.util.annotation.Nullable;

import java.util.List;

public class PermissionUtil {

    public static synchronized boolean canAccess(Member member, String repo){
        return canAccess (member.getId (), repo) || member.getRoleIds ().stream ().anyMatch (id -> canAccess (id, repo));
    }

    public static synchronized boolean canAccess(Snowflake id, String repo){
        List<String> repos = getRepos(id);
        return repos != null && (repos.size () == 0 || repos.contains (repo));
    }

    private static @Nullable List<String> getRepos(Snowflake id){
        List<PermissionEntry> grants = StartGuardian.cfg.grants;

        for(PermissionEntry grant : grants){
            if(grant.id == id.asLong ()){
                return grant.repos;
            }
        }
        return null;
    }
}
