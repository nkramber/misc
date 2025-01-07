import java.util.Scanner;

public class BinarySearchTree {
    Scanner scanner = new Scanner(System.in);
    //Starting variables for the Binary Search Tree
    int[] starterKeys = {4, 2, 6, 1, 3, 5, 7};
    public static void main(String[] args) {
        //Create instance of BinarySearchTree class and execute the run method to start the program
        BinarySearchTree tree = new BinarySearchTree();
        tree.run();
    }

    public void run() {
        boolean running = true;
        int input;
        BinaryTree binaryTree = null;

        while (running) {
            System.out.println("\nPlease select an option by typing the number and pressing enter\n");
            System.out.println("1. Create a binary search tree");
            System.out.println("2. Add a node");
            System.out.println("3. Delete a node");
            System.out.println("4. Print nodes by InOrder");
            System.out.println("5. Print nodes by PreOrder");
            System.out.println("6. Print nodes by PostOrder");
            System.out.println("7. Exit Program\n");

            //Get input from user to determine menu option selection
            try {
                input = scanner.nextInt();
                System.out.println("");
            } catch (Exception e) {
                System.out.println("Please enter a valid option");
                scanner.nextLine();
                continue;
            }

            if (input == 1) {
                //Create new tree with starter keys
                binaryTree = initBinaryTree(starterKeys);
                System.out.println("Binary search tree created");
                continue;
            } else if (input == 2) {
                //Add key to tree
                if (binaryTree == null) {
                    System.out.println("Binary search tree not yet created");
                    continue;
                } else {
                    System.out.println("Please enter the key value you wish to add\n");
                    try {
                        input = scanner.nextInt();
                        System.out.println("");
                    } catch (Exception e) {
                        System.out.println("Please enter a positive integer");
                        scanner.nextLine();
                        continue;
                    }

                    binaryTree.add(input);
                }
            } else if (input == 3) {
                //Delete key from tree
                System.out.println("Please enter a key to delete\n");
                try {
                    input = scanner.nextInt();
                    System.out.println("");
                } catch (Exception e) {
                    System.out.println("Please enter a positive integer");
                    scanner.nextLine();
                    continue;
                }

                binaryTree.delete(input);
            } else if (input == 4) {
                //Print tree by InOrder
                if (binaryTree == null) {
                    System.out.println("Binary search tree not yet created");
                    continue;
                } else {
                    binaryTree.printInOrder(binaryTree.getRoot());
                    System.out.println("");
                }
            } else if (input == 5) {
                //Print tree by PreOrder
                if (binaryTree == null) {
                    System.out.println("Binary search tree not yet created");
                    continue;
                } else {
                    binaryTree.printPreOrder(binaryTree.getRoot());
                    System.out.println("");
                }
            } else if (input == 6) {
                //Print tree by PostOrder
                if (binaryTree == null) {
                    System.out.println("Binary search tree not yet created");
                    continue;
                } else {
                    binaryTree.printPostOrder(binaryTree.getRoot());
                    System.out.println("");
                }
            } else if (input == 7) {
                //End program
                running = false;
            }
        }
    }

    public BinaryTree initBinaryTree(int[] keys) {
        //Create the tree with initial values
        BinaryTree binaryTree = new BinaryTree(keys[0]);
        for (int i = 1; i < keys.length; i++) {
            binaryTree.add(keys[i]);
        }

        return binaryTree;
    }

    public static class BinaryTree {
        //This is the source node, in this case it will be 4 to start once it is initialized
        Node root;
        
        BinaryTree(int key) {
            root = new Node(key);
        }

        //Add node to the tree
        public void add(int key) {
            root = addNode(root, key);
        }

        //Private recursive method to add node to the tree
        private Node addNode(Node node, int key) {
            if (node == null) {
                node = new Node(key);
                return node;
            } else if (key < node.getKey()) {
                node.setLeft(addNode(node.getLeft(), key));
            } else if (key > node.getKey()) {
                node.setRight(addNode(node.getRight(), key));
            }

            return node;
        }

        //Delete node from the tree
        public void delete(int key) {
            root = deleteNode(root, key);
        }

        //Private recursive method to remove node from the tree
        private Node deleteNode(Node root, int key) {
            if (root == null) {
                //Node doesn't exist, return null
                return null;
            } else if (key < root.getKey()) {
                //Key lies to the left of the root, execute recursive method again with left node as root
                root.setLeft(deleteNode(root.getLeft(), key));
            } else if (key > root.getKey()) {
                //Key lies to the right of the root, execute recursive method again with right node as root
                root.setRight(deleteNode(root.getRight(), key));
            } else {
                //Key = root key, we have found the one we want to delete
                if (root.getLeft() == null && root.getRight() == null) {
                    //Key to be deleted has to children so we can delete it now
                    return null;
                } else if (root.getLeft() == null) {
                    //Key has no left children
                    return root.getRight();
                } else if (root.getRight() == null) {
                    //Key has no right children
                    return root.getLeft();
                }

                //Key has both left and right children. Find the minimum right child and replace the key to be deleted with it
                Node min = findMin(root.getRight());
                root.setKey(min.getKey());
                root.setRight(deleteNode(root.getRight(), min.getKey()));
            }

            return root;
        }

        //Find the minimum value of the remaining branches
        private Node findMin(Node root) {
            while (root.getLeft() != null) {
                root = root.getLeft();
            }

            return root;
        }

        //Print the values of the tree via InOrder
        public void printInOrder(Node root) {
            if (root == null) {
                return;
            } else {
                printInOrder(root.getLeft());
                System.out.print(root.getKey() + " ");
                printInOrder(root.getRight());
            }
        }

        //Print the values of the tree via PreOrder
        public void printPreOrder(Node root) {
            if (root == null) {
                return;
            } else {
                System.out.print(root.getKey() + " ");
                printPreOrder(root.getLeft());
                printPreOrder(root.getRight());
            }
        }

        //Print the values of the tree via PostOrder
        public void printPostOrder(Node root) {
            if(root == null) {
                return;
            } else {
                printPostOrder(root.getLeft());
                printPostOrder(root.getRight());
                System.out.print(root.getKey() + " ");
            }
        }

        public Node getRoot() { return root; }
    }

    public static class Node {
        int key;
        Node left, right;
        
        //Initial constructor
        public Node(int key) {
            this.key = key;
            left = null;
            right = null;
        }

        //Getters and setters
        public int getKey() { return key; }
        public void setKey(int key) { this.key = key; }
        public Node getRight() { return right; }
        public Node getLeft() { return left; }
        public void setRight(Node right) { this.right = right; }
        public void setLeft(Node left) { this.left = left; }
    }
}