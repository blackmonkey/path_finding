/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Elias Nygren
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding;

import darkstudio.pathfinding.utility.GridNav;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Tests for Cartographer functionality.
 */
public class CartographerFunctionalityTest {
    private File f1;
    private File f2;
    private File f3;
    private File f4;
    private GridNav gridNav;

    @Before
    public void setUp() {
        gridNav = new GridNav();
        f1 = new File("test1.map");
        f2 = new File("test2.map");
        f3 = new File("test3.map");
        f4 = new File("asdasdasd"); //does not exist
    }

    @Test
    public void testLoadMap() {
        Assert.assertTrue("failed to load file " + f1.getAbsolutePath(), gridNav.loadMap(f1));
        assertEquals('T', gridNav.getVertices()[0][0].getKey());
        assertEquals('@', gridNav.getVertices()[1][0].getKey());

        Assert.assertTrue("failed to load file " + f2.getAbsolutePath(), gridNav.loadMap(f2));
        assertEquals('@', gridNav.getVertices()[0][0].getKey());
        assertEquals('T', gridNav.getVertices()[0][3].getKey());
        assertEquals('T', gridNav.getVertices()[1][0].getKey());
        assertEquals('T', gridNav.getVertices()[2][3].getKey());

        Assert.assertFalse("should NOT successfully load file " + f3.getAbsolutePath(), gridNav.loadMap(f3));
        Assert.assertFalse("should NOT load non-exist file " + f4.getAbsolutePath(), gridNav.loadMap(f4));
    }
}