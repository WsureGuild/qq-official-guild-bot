package top.wsure.guild.bot.utils.ac

/**
 * FileName: MyAC
 * Author:   wsure
 * Date:     2021/4/9 10:19 上午
 * Description:
 */
class AhoCorasickMatcher<T>(patterns: Collection<T>,val getKey: (T) -> String) {
    val rootNode = constructACAutomaton(patterns)

    private fun createTrie(root: AcNode<T>, wordList: MutableSet<T>): AcNode<T> {

        wordList.forEach { word ->
            var p = root
            getKey(word).toCharArray().forEach { char ->
                var child = p.children[char]
                if (child == null) {
                    child = AcNode(value = char)
                    p.children[char] = child
                }
                p = child
            }
            p.key = word
            p.finished = true
        }
        return root
    }

    private fun createAutomaton(root: AcNode<T>){
        val deque:ArrayDeque<AcNode<T>> = ArrayDeque()
        deque.addLast(root)
        while (deque.isNotEmpty()){
            val node = deque.removeFirst()
            node.children.forEach{ (value, child) ->
                if(node == root){
                    child.fail = root
                } else {
                    val failNode = node.fail
                    val char = failNode?.children?.get(value)
                    if(char != null){
                        child.fail = char
                    } else {
                        child.fail = root
                    }
                }
                deque.addLast(child)
            }
        }
    }

    fun search(text:String):List<T>{
        var node : AcNode<T>? = rootNode
        val result = arrayListOf<MatchingResult<T>>()
        text.toCharArray().forEachIndexed{ i,c ->
            while (node != null && node!!.children[c] == null){
                node = node!!.fail
            }
            if(node == null){
                node = rootNode
                return@forEachIndexed
            }
            node = node!!.children[c]
            var out = node
            while (out != null){
                if(out.finished){
                    result.add(MatchingResult(getKey(out.key!!),i,out.key!!))
                }
                out = out.fail
            }
        }
        return result.map { it.pattern }
    }

    private fun constructACAutomaton(patterns: Collection<T>): AcNode<T> {
        val root = AcNode<T>()
        this.createTrie(root, patterns.toMutableSet())
        this.createAutomaton(root)
        return root
    }

}