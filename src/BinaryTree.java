import java.io.*;
import java.util.Iterator;

public class BinaryTree<E extends Comparable<E>> {
    private TreeNode<E> root;
    private int size = 0;

    /**
     * Leerer Konstruktor
     */
    public BinaryTree() {
    }

    /**
     * Erstellt einen BinaryTree aus einem Array von Objekten eines beliebigen Typs
     */
    public BinaryTree(E[] objects) {
        for (int i = 0; i < objects.length; i++) {
            insert(objects[i]);
        }
    }

    /**
     * Returns true wenn das Element im Baum gefunden wurde
     */
    public boolean search(E e) {
        TreeNode<E> current = root; // Start von der Wurzel

        while (current != null) {
            if (e.compareTo(current.element) < 0) {
                current = current.left;
            } else if (e.compareTo(current.element) > 0) {
                current = current.right;
            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * Element wird eingefuegt
     * Returns true wenn das Element erfolgreich eingefuegt wurde
     */
    public boolean insert(E e) {
        if (root == null)
            root = createNode(e); // Create a new root
        else {
            // Parent Node
            TreeNode<E> parent = null;
            TreeNode<E> current = root;
            while (current != null)
                if (e.compareTo(current.element) < 0) {
                    parent = current;
                    current = current.left;
                } else if (e.compareTo(current.element) > 0) {
                    parent = current;
                    current = current.right;
                } else {
                    return false; // Doppelt vorhandene Nodes werden nicht hinzugefuegt
                }
            if (e.compareTo(parent.element) < 0)
                parent.left = createNode(e);
            else
                parent.right = createNode(e);
        }

        size++;
        return true; // Element wurde hinzugefuegt
    }

    /**
     * Gibt die Tiefe des Baums zurueck, indem es den Baum rekursiv durchlaeuft
     * und int depth dabei inkrementell erhoeht
     */
    public int getDepth(TreeNode<E> node) {
        if (node == null) {
            return (0);
        } else {
            // berechnet die Tiefe jedes Unterbaums
            int lDepth = getDepth(node.left);
            int rDepth = getDepth(node.right);

            // der groessere Wert wird als Ergebnis zurueckgegeben
            if (lDepth > rDepth) {
                return (lDepth + 1);
            }
            else {
                return (rDepth + 1);
            }
        }
    }

    /**
     * Erstellt einen neuen Node
     */
    protected TreeNode<E> createNode(E e) {
        return new TreeNode<E>(e);
    }

    /**
     * inOrder traversal from the root
     */
    public void inOrder() {
        inOrder(root);
    }

    /**
     * inOrder traversal from a subtree
     */
    protected void inOrder(TreeNode<E> root) {
        if (root == null) return;
        inOrder(root.left);
        System.out.print(root.element + " ");
        inOrder(root.right);
    }

    /**
     * Innere Klasse TreeNode<E>
     * Beinhaltet widerrum einen linke und rechte Kindnode
     */
    public static class TreeNode<E extends Comparable<E>> {
        E element;
        TreeNode<E> left;
        TreeNode<E> right;
        public TreeNode(E e) {
            element = e;
        }
    }

    /**
     * Die Anzahl der Nodes im Baum
     */
    public int getSize() {
        return size;
    }

    /**
     * Gibt die Wurzel des Baums zurueck
     */
    public TreeNode getRoot() {
        return root;
    }

    /**
     * Speichert den Baum in eine Datei (Elemente werden einfach nacheinander in die Datei geschrieben)
     */
    public void saveToFile() {
        try {
            PrintWriter writer = new PrintWriter("output.dat", "UTF-8");
            Iterator<String> iterator = this.iterator();
            while (iterator.hasNext()) {
                writer.println(iterator.next().toString());
            }
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Laed einen Baum aus einer Datei
     */
    public void loadFromFile() {
        try {
            FileReader in = new FileReader("output.dat");
            BufferedReader br = new BufferedReader(in);
            clear();
            String elem;
            while ((elem = br.readLine()) != null) {
                insert((E)(Object)elem);
            }
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loescht ein Element aus dem Baum
     * Returns true wenn das Element erfolgreich geloescht werden konnte
     * Returns false wenn das Element nicht im Baum gefunden wurde
     */
    public boolean delete(E e) {
        TreeNode<E> parent = null;
        TreeNode<E> current = root;
        while (current != null) {
            if (e.compareTo(current.element) < 0) {
                parent = current;
                current = current.left;
            } else if (e.compareTo(current.element) > 0) {
                parent = current;
                current = current.right;
            } else
                break; // Element wurde im Baum gefunden
        }

        if (current == null)
            return false; // Element wurde nicht gefunden

        // Fall 1: current hat keine linke Kindnode
        if (current.left == null) {
            // Die Vorgaengernode der current node wird mit der rechten Kindnode
            // der current node verbunden
            if (parent == null) {
                root = current.right;
            } else {
                if (e.compareTo(parent.element) < 0)
                    parent.left = current.right;
                else
                    parent.right = current.right;
            }
        } else {
            // Fall 2: current node hat eine linke Kindnode
            // Lokalisiere die am weitetsten rechts gelegene node im linken subbaum
            // der current node und seiner Vorgaengernodes
            TreeNode<E> parentOfRightMost = current;
            TreeNode<E> rightMost = current.left;

            while (rightMost.right != null) {
                parentOfRightMost = rightMost;
                rightMost = rightMost.right; // weiter nach rechts
            }

            // Element wird durch das am weitetsten rechts gefundene ersetzt
            current.element = rightMost.element;

            // Die am weitetsten rechts gefunden Node wird geloescht
            if (parentOfRightMost.right == rightMost)
                parentOfRightMost.right = rightMost.left;
            else
                // Spzeialfall: das zu loeschende Element ist bereits
                // das am weitesten rechts gerichtete Element
                parentOfRightMost.left = rightMost.left;
        }
        size--;
        return true;
    }

    /**
     * Gibt custom Iterator zurueck.
     */
    public java.util.Iterator iterator() {
        return new OrderedIterator();
    }

    /**
     * Eingebettete Klasse, die es erlaubt durch den Baum zu iterieren
     */
    class OrderedIterator implements java.util.Iterator {

        private java.util.ArrayList<E> list = new java.util.ArrayList<E>();
        private int current = 0;

        /**
         * Konstruktor befuellt die Liste in der alle Elemente des Baums gespeichert werden
         */
        public OrderedIterator() {
            inOrder(); // Traversiert durch den Baum und speichert die Elemente in einer Liste
        }

        /**
         * Geht traversal vom Wurzelknoten aus nach unten
         */
        private void inOrder() {
            inOrder(root);
        }

        /**
         * Geht traversal vom einem gegeben Knoten aus nach unten
         */
        private void inOrder(TreeNode<E> root) {
            if (root == null) return;
            inOrder(root.left);
            list.add(root.element);
            inOrder(root.right);
        }

        /**
         * Gibt es noch weitere Elemente?
         */
        public boolean hasNext() {
            if (current < list.size())
                return true;

            return false;
        }

        /**
         * Gibt das aktuelle element zurueck und zaehlt weiter zum naechsten
         */
        public Object next() {
            return list.get(current++);
        }

        /**
         * Entfernt das aktuelle Element und laed den Baum neu
         */
        public void remove() {
            delete(list.get(current)); // Delete the current element
            list.clear(); // Clear the list
            inOrder(); // Rebuild the list
        }
    }

    /**
     * Entfernt alle Elemente aus dem Baum (Reset)
     */
    public void clear() {
        root = null;
        size = 0;
    }
}