//package io.github.qtilvi.challenges.command;
//
//import io.github.qtilvi.challenges.challenge.Challenge;
//import io.github.qtilvi.challenges.manager.ChallengeManager;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.jetbrains.annotations.NotNull;
//import org.jspecify.annotations.NonNull;
//
//public class ChallengeCommand implements CommandExecutor {
//    private final ChallengeManager challengeManager;
//
//    public ChallengeCommand(ChallengeManager challengeManager) {
//        this.challengeManager = challengeManager;
//    }
//
//    @Override
//    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String label, @NotNull String @NotNull [] args) {
//        if (args.length != 2) return false;
//        Challenge challenge = challengeManager.get(args[1]);
//        if (challenge == null) {
//            commandSender.sendMessage("Unknown challenge.");
//            return true;
//        }
//        if (args[0].equalsIgnoreCase("enable")) {
//            challenge.enable();
//            commandSender.sendMessage("Enabled " + challenge.getName());
//        }
//        if (args[0].equalsIgnoreCase("disable")) {
//            challenge.enable();
//            commandSender.sendMessage("Disabled " + challenge.getName());
//        }
//        return true;
//    }
//}
