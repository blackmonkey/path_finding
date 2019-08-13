/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Shane
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.ui;

import darkstudio.pathfinding.model.Grid;
import darkstudio.pathfinding.model.GridNode;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends JFrame implements MouseListener {
    private final int X = 30, Y = 40;
    private int[][] map;
    private boolean isGreen, isRed;

    private ArrayList<JButton> path;
    private JButton[][] buttons;
    private AtomicBoolean mousePressed;
    private GridNode[][] nodes;
    private GridNode startNode;
    private GridNode endNode;
    private Grid grid;

    private final int /*LIGHT_GREY = 0,*/ DARK_GREY = 1, GREEN = 2, RED = 3;

    private Color PATH = Color.ORANGE;

    private Color[] backgrounds = {
            Color.LIGHT_GRAY,
            Color.DARK_GRAY,
            Color.GREEN,
            Color.RED
    };

    public static void main(String[] args) {
        new Main();
    }

    private Main() {
        super();

        buttons = new JButton[X][Y];
        nodes = new GridNode[X][Y];
        path = null;
        grid = new Grid(nodes);

        mousePressed = new AtomicBoolean(false);

        map = new int[X][Y];
        isGreen = false;
        isRed = false;

        setTitle("Jump Point Search");
        setSize((Y * 25), (X * 25));
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(createMainPanel());
        setVisible(true);
    }

    private Container createMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        JPanel grid = new JPanel(new GridLayout(X, Y));

        for (int x = 0; x < X; x++) {
            for (int y = 0; y < Y; y++) {
                JButton button = new JButton();
                button.setBackground(Color.LIGHT_GRAY);
                button.setActionCommand(x + "-" + y);
                button.addMouseListener(this);
                grid.add(button);

                buttons[x][y] = button;
                nodes[x][y] = new GridNode(x, y, true);
            }
        }

        JButton button = new JButton("Search");
        button.addActionListener(evt -> searchPath());

        main.add(grid, BorderLayout.CENTER);
        main.add(button, BorderLayout.SOUTH);

        main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return main;
    }

    private void searchPath() {
        if (!isGreen || !isRed) {
            JOptionPane.showMessageDialog(this, "Start and/or ending point undefined", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<GridNode> result = grid.searchJPS(startNode, endNode);

        if (result == null) {
            JOptionPane.showMessageDialog(this, "No path available", "Information", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Iterator<GridNode> resultIt = result.iterator();
            path = new ArrayList<>();
            GridNode aux = null;
            while (resultIt.hasNext()) {
                GridNode node = resultIt.next();
                drawLine(aux, node);
                aux = node;
            }
        }
    }

    private void drawLine(GridNode n0, GridNode n1) {
        if (n0 == null || n1 == null) {
            return;
        }

        int x = n0.getX(), y = n0.getY(), toX = n1.getX(), toY = n1.getY();

        // Straight lines
        if (y == toY) {
            if (x < toX) {
                while (x != toX) {
                    buttons[x][y].setBackground(PATH);
                    path.add(buttons[x][y]);
                    x++;
                }
            } else {
                while (x != toX) {
                    buttons[x][y].setBackground(PATH);
                    path.add(buttons[x][y]);
                    x--;
                }
            }
        } else if (x == toX) {
            if (y < toY) {
                while (y != toY) {
                    buttons[x][y].setBackground(PATH);
                    path.add(buttons[x][y]);
                    y++;
                }
            } else {
                while (y != toY) {
                    buttons[x][y].setBackground(PATH);
                    path.add(buttons[x][y]);
                    y--;
                }
            }
        } else if (x < toX) {    // Diagonals lines
            if (y < toY) {
                while (x != toX && y != toY) {
                    buttons[x][y].setBackground(PATH);
                    path.add(buttons[x][y]);
                    y++;
                    x++;
                }
            } else {
                while (x != toX && y != toY) {
                    buttons[x][y].setBackground(PATH);
                    path.add(buttons[x][y]);
                    y--;
                    x++;
                }
            }
        } else {
            if (y < toY) {
                while (x != toX && y != toY) {
                    buttons[x][y].setBackground(PATH);
                    path.add(buttons[x][y]);
                    y++;
                    x--;
                }
            } else {
                while (x != toX && y != toY) {
                    buttons[x][y].setBackground(PATH);
                    path.add(buttons[x][y]);
                    y--;
                    x--;
                }
            }
        }
    }

    private void changeBackground(JButton b) {
        String[] index = b.getActionCommand().split("-");
        int i = Integer.parseInt(index[0]), a = Integer.parseInt(index[1]);
        map[i][a] = getNextBackground(i, a);
        b.setBackground(backgrounds[map[i][a]]);
    }

    private int getNextBackground(int x, int y) {
        int current = map[x][y];

        if (current == GREEN) {
            isGreen = false;
            startNode = null;
        } else if (current == RED) {
            isRed = false;
            endNode = null;
        }

        current++;

        if (current >= backgrounds.length) {
            current = 0;
        } else if (current == GREEN && isGreen) {
            if (isRed) {
                current = 0;
            } else {
                current = RED;
                isRed = true;
                endNode = nodes[x][y];
            }
        } else if (current == RED && isRed) {
            current = 0;
        } else if (current == GREEN) {
            isGreen = true;
            startNode = nodes[x][y];
        } else if (current == RED) {
            isRed = true;
            endNode = nodes[x][y];
        }

        if (current == DARK_GREY) {
            nodes[x][y].setPassable(false);
        } else {
            nodes[x][y].setPassable(true);
        }

        return current;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        synchronized (mousePressed) {
            if (mousePressed.get()) {
                JButton b = (JButton) e.getSource();
                changeBackground(b);
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        synchronized (mousePressed) {
            if (path != null) {
                for (JButton button : path) {
                    String[] coord = button.getActionCommand().split("-");
                    map[Integer.parseInt(coord[0])][Integer.parseInt(coord[1])] = 0;
                    button.setBackground(Color.LIGHT_GRAY);
                    isRed = false;
                }
                path = null;
            }
            JButton b = (JButton) e.getSource();
            changeBackground(b);
            mousePressed.set(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        synchronized (mousePressed) {
            mousePressed.set(false);
        }
    }
}
