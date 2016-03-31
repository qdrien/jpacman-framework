package nl.tudelft.jpacman.sprite;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies the loading of sprites.
 *
 * @author Jeroen Roosen
 */
@SuppressWarnings("magicnumber")
public class SpriteTest {

    private final int spriteSize = 64;
    private Sprite sprite;
    private SpriteStore store;

    /**
     * The common fixture of this test class is
     * a 64 by 64 pixel white sprite.
     *
     * @throws java.io.IOException when the sprite could not be loaded.
     */
    @Before
    public void setUp() throws IOException {
        store = new SpriteStore();
        sprite = store.loadSprite("/sprite/64x64white.png");
    }

    /**
     * Verifies the width of a static sprite.
     */
    @Test
    public void spriteWidth() {
        assertEquals(spriteSize, sprite.getWidth());
    }

    /**
     * Verifies the height of a static sprite.
     */
    @Test
    public void spriteHeight() {
        assertEquals(spriteSize, sprite.getHeight());
    }

    /**
     * Verifies that an IOException is thrown when the resource could not be
     * loaded.
     *
     * @throws java.io.IOException since the sprite cannot be loaded.
     */
    @Test(expected = IOException.class)
    public void resourceMissing() throws IOException {
        store.loadSprite("/sprite/nonexistingresource.png");
    }

    /**
     * Verifies that an animated sprite is correctly cut from its base image.
     */
    @Test
    public void animationWidth() {
        assertEquals(16, store.createAnimatedSprite(sprite, 4, 0, false).getWidth());
    }

    /**
     * Verifies that an animated sprite is correctly cut from its base image.
     */
    @Test
    public void animationHeight() {
        assertEquals(64, store.createAnimatedSprite(sprite, 4, 0, false).getHeight());
    }

    /**
     * Verifies that an split sprite is correctly cut from its base image.
     */
    @Test
    public void splitWidth() {
        assertEquals(12, sprite.split(10, 11, 12, 13).getWidth());
    }

    /**
     * Verifies that an split sprite is correctly cut from its base image.
     */
    @Test
    public void splitHeight() {
        assertEquals(13, sprite.split(10, 11, 12, 13).getHeight());
    }

    /**
     * Verifies that a split that isn't within the actual sprite returns an empty sprite.
     */
    @Test
    public void splitOutOfBounds() {
        assertTrue(sprite.split(10, 10, 64, 10) instanceof EmptySprite);
    }
}
