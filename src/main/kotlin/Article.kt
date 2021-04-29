data class Article(
    val id: Int,
    val regDate: String,
    var updateDate: String,
    var title: String,
    var body: String,
    var memberId:Int=0,
    var boardId:Int

)