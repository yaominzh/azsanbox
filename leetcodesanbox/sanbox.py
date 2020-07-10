class TreeNode():
    def __init__(self, val):
        self.val= val 
        self.left = None
        self.right  = None
    
def preOrderTraverse(node):
    if not node:
        return None
    print(str(node.val))
    preOrderTraverse(node.left)
    preOrderTraverse(node.right)
    


root=TreeNode(5)
root.left = TreeNode(4)
root.right= TreeNode(7)

preOrderTraverse(root)
