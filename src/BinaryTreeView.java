import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class BinaryTreeView extends JPanel {
    private BinaryTree<String> tree; // Der BinaryTree der dargestellt bzw. befuellt wird
    private JTextField jtfKey = new JTextField(5);
    private PaintTree paintTree = new PaintTree();

    //Buttons
    private JButton jbtInsert = new JButton("Einfügen");
    private JButton jbtDelete = new JButton("Löschen");
    private JButton jbtSave = new JButton("Speichern");
    private JButton jbtLoad= new JButton("Laden");
    private JButton jbtReset = new JButton("Reset");


    private JLabel jlbDepth = new JLabel("0");
    private JLabel jlbSize = new JLabel("0");

    public BinaryTreeView(BinaryTree<String> tree) {
        this.tree = tree; // Set a binary tree to be displayed
        setUI();
    }

    /** Initialisiert das UI fuer den Binary Tree */
    private void setUI() {
        this.setLayout(new BorderLayout());
        add(paintTree, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        // Die vielen Leerzeichen in den JLabels kommen von Schwierigkeiten mit dem Layout
        // Kann man auch besser lösen
        panel.add(new JLabel("Nodes: "));
        panel.add(jlbSize);
        panel.add(new JLabel("  Tiefe: "));
        panel.add(jlbDepth);
        panel.add(new JLabel("        Wert: "));
        panel.add(jtfKey);
        panel.add(jbtInsert);
        panel.add(jbtDelete);
        panel.add(new JLabel("    "));
        panel.add(jbtSave);
        panel.add(jbtLoad);
        panel.add(new JLabel("    "));
        panel.add(jbtReset);
        add(panel, BorderLayout.SOUTH);

        //Event Listener fuer die Buttons
        jbtInsert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String key = jtfKey.getText();
                if (tree.search(key)) { // Key wurde im Baum gefunden
                    JOptionPane.showMessageDialog(null, key + " ist bereits vorhanden");
                }
                else {
                    if (key.length() <= 3 && key.length() > 0) {
                        tree.insert(key);
                        paintTree.repaint();
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Der Wert ist zu lang oder leer");
                    }
                }
            }
        });

        jbtDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String key = jtfKey.getText();
                if (!tree.search(key)) { // key is not in the tree
                    JOptionPane.showMessageDialog(null,
                            key + " ist nicht vorhanden");
                }
                else {
                    tree.delete(key); // Delete a key
                    paintTree.repaint(); // Redisplay the tree
                }
            }
        });

        jbtSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tree.saveToFile();
                JOptionPane.showMessageDialog(null,"Baum wurde gespeichert.");
            }
        });

        jbtLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tree.loadFromFile();
                paintTree.repaint(); // Redisplay the tree
                JOptionPane.showMessageDialog(null,"Baum wurde geladen.");
            }
        });

        jbtReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tree.clear();
                paintTree.repaint(); // Redisplay the tree
            }
        });


    }

    // Eingebette Klasse PaintTree, die die Darstellung der Nodes und die Verbindungslinien zeichnet
    class PaintTree extends JPanel {
        private int radius = 20; // Radius eines Nodes
        private int vGap = 50; // vertikaler Abstand zwischen den Ebenen

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (tree.getRoot() != null) {
                // Baum wird rekursiv aufgebaut
                displayTree(g, tree.getRoot(), getWidth() / 2, 30, getWidth() / 4);
            }
            jlbDepth.setText(String.valueOf(tree.getDepth(tree.getRoot())));
            jlbSize.setText(String.valueOf(tree.getSize()));
        }

        /** Zeichnet den Baum ausgehend von einem Subtree nach unten */
        private void displayTree(Graphics g, BinaryTree.TreeNode root, int x, int y, int hGap) {
            // Stellt die Wurzel dar
            g.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
            g.drawString(root.element + "", x - 6, y + 4);

            if (root.left != null) {
                // Zeichnet eine Linie zur linken Node
                connectLeftChild(g, x - hGap, y + vGap, x, y);
                // Der linke Subtree wird rekursiv weitergezeichnet
                displayTree(g, root.left, x - hGap, y + vGap, hGap / 2);
            }

            if (root.right != null) {
                // Zeichnet eine Linie zur rechten Node
                connectRightChild(g, x + hGap, y + vGap, x, y);
                // Der rechte Subtree wird rekursiv weitergezeichnet
                displayTree(g, root.right, x + hGap, y + vGap, hGap / 2);
            }
        }

        /** Verbindet einen Knoten (x2, y2) mit
         * seinem linken Kindknoten (x1, y1) */
        private void connectLeftChild(Graphics g, int x1, int y1, int x2, int y2) {
            double d = Math.sqrt(vGap * vGap + (x2 - x1) * (x2 - x1));
            int x11 = (int)(x1 + radius * (x2 - x1) / d);
            int y11 = (int)(y1 - radius * vGap / d);
            int x21 = (int)(x2 - radius * (x2 - x1) / d);
            int y21 = (int)(y2 + radius * vGap / d);
            g.drawLine(x11, y11, x21, y21);
        }

        /** Verbindet einen Knoten (x2, y2) mit
         * seinem rechten Kindknoten (x1, y1) */
        private void connectRightChild(Graphics g, int x1, int y1, int x2, int y2) {
            double d = Math.sqrt(vGap * vGap + (x2 - x1) * (x2 - x1));
            int x11 = (int)(x1 - radius * (x1 - x2) / d);
            int y11 = (int)(y1 - radius * vGap / d);
            int x21 = (int)(x2 + radius * (x1 - x2) / d);
            int y21 = (int)(y2 + radius * vGap / d);
            g.drawLine(x11, y11, x21, y21);
        }
    }

    /** Mainmethode setzt das Ganze in Gang
     *  Hier kann auch ein bereits vorhandener BinaryTree uebergeben werden*/
    public static void main(String[] args) {
        JFrame frame = new JFrame("Binary Tree");
        JPanel applet = new BinaryTreeView(new BinaryTree<String>()); //Leerer BinaryTree wird dem Konstruktor übergeben
        frame.add(applet);
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}