package com.github.smuddgge.leaf.utility;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a collection of
 * discord utility methods.
 */
public class DiscordUtility {

    /**
     * Used to check if a member has a role
     * from a given list.
     *
     * @param list   The instance of the list.
     * @param member The instance of the member.
     * @return True if the member has one of the roles
     * or if there are no roles in the list.
     */
    public static boolean hasRoleFromList(@NotNull List<String> list, @NotNull Member member) {
        // Check if there are no roles in the list.
        if (list.isEmpty()) return true;

        // Loop though all possible role names.
        for (String roleName : list) {

            // Loop though the members roles.
            for (Role role : member.getRoles()) {

                // Check if the member has the role.
                if (roleName.equalsIgnoreCase(role.getName())) return true;
            }
        }
        return false;
    }
}
