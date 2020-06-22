class Solution():
    def isPalindrome(self, x: int) -> bool:
        l = list(str(x))
        if l[0] == '-':
            return False
        if l == list.reverse(l):
            return True
        return False

if __name__ == "__main__":
    print(Solution().isPalindrome(121))
