package nl.tudelft.jpacman.level;

/**
 * Interface to be implemented by classes interested in Player-related events such as losing a life
 */
interface PlayerListener {

    /**
     * Called whenever a player loses a life
     *
     * @param p The Player that just lost one life
     */
    void onPlayerLoseLife(Player p);
}
