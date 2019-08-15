/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Elias Nygren
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ToolsTest {
    private File f1;
    private File f2;
    private File f3;
    private File f4;
//    private GridNav gridNav;
//
//    @Before
//    public void setUp() {
//        gridNav = new GridNav();
//        f1 = new File(getClass().getClassLoader().getResource("test1.map").getFile());
//        f2 = new File(getClass().getClassLoader().getResource("test2.map").getFile());
//        f3 = new File(getClass().getClassLoader().getResource("test3.map").getFile());
//        f4 = new File("asdasdasd"); // does not exist
//    }
//
//    @Test
//    public void testLoadMap() {
//        Assert.assertTrue("failed to load file " + f1.getAbsolutePath(), gridNav.loadMap(f1));
//        assertEquals('T', gridNav.getMap()[0][0].getKey());
//        assertEquals('@', gridNav.getMap()[1][0].getKey());
//
//        Assert.assertTrue("failed to load file " + f2.getAbsolutePath(), gridNav.loadMap(f2));
//        assertEquals('@', gridNav.getMap()[0][0].getKey());
//        assertEquals('T', gridNav.getMap()[0][3].getKey());
//        assertEquals('T', gridNav.getMap()[1][0].getKey());
//        assertEquals('T', gridNav.getMap()[2][3].getKey());
//
//        Assert.assertFalse("should NOT successfully load file " + f3.getAbsolutePath(), gridNav.loadMap(f3));
//        Assert.assertFalse("should NOT load non-exist file " + f4.getAbsolutePath(), gridNav.loadMap(f4));
//    }
}