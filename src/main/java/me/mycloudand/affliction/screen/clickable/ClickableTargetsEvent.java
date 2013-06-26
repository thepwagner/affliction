package me.mycloudand.affliction.screen.clickable;

import me.mycloudand.affliction.model.Pixel;

import java.util.Set;

/**
 * New targets have been found.
 */
public class ClickableTargetsEvent {
    private final Set<Pixel> targets;

    public ClickableTargetsEvent(Set<Pixel> targets) {
        this.targets = targets;
    }

    public Set<Pixel> getTargets() {
        return targets;
    }
}
