object BoardController{
    fun write(login:Int){

        BoardRepository.writeBoard(login)
    }
    fun list(){
        val boards = BoardRepository.listBoard()


        for (board in boards) {

            println("${board.num}, ${board.boardName}, ${board.boardId}")
        }
    }
}