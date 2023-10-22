package pl.edu.pb.wi

class Question(private val questionId: Int, private val trueAnswer: Boolean) {
    fun getQuestionId(): Int {
        return questionId;
    }

    fun isTrueAnswer(): Boolean {
        return trueAnswer
    }
}
