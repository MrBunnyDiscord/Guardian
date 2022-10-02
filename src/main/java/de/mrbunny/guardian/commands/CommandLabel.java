package de.mrbunny.guardian.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mrbunny.guardian.util.GHInstallation;
import de.mrbunny.guardian.util.GithubUtil;
import de.mrbunny.guardian.util.PermissionUtil;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.HttpException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Optional;

public class CommandLabel implements Command{

    public Mono<?> invoke(Context ctx) throws CommandException {

        String[] args = ctx.getArgs();

        GHInstallation installation;
        try {
            installation = GHInstallation.fromConfig();
        } catch (GHInstallation.NoSuchInstallationException e) {
            try {
                installation = GHInstallation.org(args[0]);
            } catch (GHInstallation.NoSuchInstallationException e1) {
                try {
                    installation = GHInstallation.repo(args[0], args[1]);
                } catch (GHInstallation.NoSuchInstallationException e2) {
                    return ctx.error("No such repository, or no installation on that repository.", e);
                } catch (ArrayIndexOutOfBoundsException e2) {
                    return ctx.error("Not enough arguments");
                }
            } catch (ArrayIndexOutOfBoundsException e1) {
                return ctx.error("Not enough arguments");
            }
        }

        Optional<String> defaultInstallation = GithubUtil.defaultInstallation();
        String repoName;
        if (defaultInstallation.isPresent() && (GithubUtil.forceDefault() || args.length < 2)) {
            repoName = defaultInstallation.get();
            if (repoName.indexOf('/') < 0) {
                repoName += args[0];
                ctx = ctx.stripArgs(1);
            }
        } else {
            repoName = args[0] + "/" + args[1];
            ctx = ctx.stripArgs(2);
        }

        if (!PermissionUtil.canAccess(ctx.getMember().block(), repoName)) {
            return ctx.error("No permission to access that repository.");
        }

        try {
            GHRepository repo = installation.getClient().getRepository(repoName);
            repo.getIssue(1).setLabels(ctx.getArgs());
        } catch (HttpException e) {
            try {
                return ctx.error(new ObjectMapper ().readTree(e.getMessage()).get("message").asText());
            } catch (IOException e1) {
                return ctx.error(e1);
            }
        } catch (IOException e) {
            return ctx.error(e);
        }

        return ctx.reply("Labels updated");
    }

}
