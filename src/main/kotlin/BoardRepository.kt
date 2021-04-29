object BoardRepository {
    private val boards = mutableListOf<Board>()
    private var lastNum = 2
    fun addBoard() {


        boards.add(Board(1,"공지","notice"))
        boards.add(Board(2,"자유","free"))
    }
    fun writeBoard(login: Int) {
        val num=++lastNum
        if (login != 0) {
            print("게시판이름 : ")
            val name = readLineTrim()
            print("코드 : ")
            val boardid = readLineTrim()

            val regDate = Util.getNowDateStr()
            val updateDate = Util.getNowDateStr()
            if (boards.any { it.boardName == name } or boards.any { it.boardId == boardid }) {
                println("중복된 값입니다")
                return
            }

            boards.add(Board(lastNum,name, boardid))
            lastNum=num
        } else {
            println("로그인 해주세요")
        }

    }
    fun getName(id:Int):String {
        for (board in boards) {
            if (board.num==id) {
                return board.boardName
            }

        }
        return "미정"
    }
    fun listBoard():List<Board>{
        return boards
    }
}