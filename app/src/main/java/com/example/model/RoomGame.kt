package com.example.model

data class TriviaQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val category: String = "General",
    val prizeCredits: Int = 50,
    val timeLimitSeconds: Int = 20
)

data class ActiveTriviaRound(
    val roomId: String,
    val question: TriviaQuestion,
    val startMillis: Long = System.currentTimeMillis(),
    val endMillis: Long = System.currentTimeMillis() + 20000L,
    val isAnswered: Boolean = false,
    val winnerUsername: String? = null
)

data class DiceGameResult(
    val playerUsername: String,
    val playerDie1: Int,
    val playerDie2: Int,
    val playerTotal: Int,
    val botDie1: Int,
    val botDie2: Int,
    val botTotal: Int,
    val betAmount: Int,
    val wonAmount: Int,
    val outcome: DiceOutcome // WIN, LOSE, TIE
)

enum class DiceOutcome {
    WIN, LOSE, TIE
}

data class CoinFlipResult(
    val playerUsername: String,
    val playerChoice: String, // "HEADS" or "TAILS"
    val outcome: String, // "HEADS" or "TAILS"
    val betAmount: Int,
    val wonAmount: Int,
    val isWin: Boolean
)

data class Lucky7Result(
    val playerUsername: String,
    val choice: String, // "LOW" (<7), "7" (Lucky 7), "HIGH" (>7)
    val die1: Int,
    val die2: Int,
    val total: Int,
    val betAmount: Int,
    val wonAmount: Int,
    val isWin: Boolean
)

object TriviaQuestionsBank {
    val QUESTIONS = listOf(
        TriviaQuestion(
            id = "q1",
            question = "ما هي المدينة التي تأسس فيها تطبيق mig33 الأصلي في عام 2005؟\nWhere was mig33 originally founded in 2005?",
            options = listOf("أديلايد - أستراليا (Adelaide)", "سنغافورة (Singapore)", "لندن (London)", "نيويورك (New York)"),
            correctOptionIndex = 0,
            category = "mig33 History",
            prizeCredits = 50
        ),
        TriviaQuestion(
            id = "q2",
            question = "ما هو الحيوان الأيقوني المستخدم كأيقونة لمصطلح الكيك Kick في شات mig33؟\nWhat iconic icon was used for Kick in mig33?",
            options = listOf("حذاء الركل 👢", "البرق ⚡", "المطرقة 🔨", "الحمار الوحشي 🦓"),
            correctOptionIndex = 0,
            category = "mig33 Nostalgia",
            prizeCredits = 40
        ),
        TriviaQuestion(
            id = "q3",
            question = "ما هي أعلى رتبة شارة إدارية نادرة في نظام الشارات الرسمي؟\nWhat is the highest and rarest Admin Special Badge?",
            options = listOf("Crown Master (099)", "Super Moderator (007)", "Community Mentor (012)", "VIP Legend (088)"),
            correctOptionIndex = 0,
            category = "mig33 Badges",
            prizeCredits = 60
        ),
        TriviaQuestion(
            id = "q4",
            question = "كم عدد الكواكب في المجموعة الشمسية؟\nHow many planets are in the Solar System?",
            options = listOf("8 كواكب", "9 كواكب", "7 كواكب", "10 كواكب"),
            correctOptionIndex = 0,
            category = "Science",
            prizeCredits = 30
        ),
        TriviaQuestion(
            id = "q5",
            question = "ما هو الرمز التعبيري الكلاسيكي لضحكة mig33 الشهيرة؟\nWhat is the classic emoticon shortcut for big laugh in mig33?",
            options = listOf(":D", "(lol)", "=))", "(rofl)"),
            correctOptionIndex = 2,
            category = "Emoticons",
            prizeCredits = 35
        ),
        TriviaQuestion(
            id = "q6",
            question = "ما هي عاصمة أستراليا؟\nWhat is the capital city of Australia?",
            options = listOf("كانبرا (Canberra)", "سيدني (Sydney)", "ملبورن (Melbourne)", "بيرث (Perth)"),
            correctOptionIndex = 0,
            category = "Geography",
            prizeCredits = 45
        ),
        TriviaQuestion(
            id = "q7",
            question = "أي من هذه الهدايا هي الأغلى في متجر الهدايا الملكية؟\nWhich of these gifts is the most expensive royal gift?",
            options = listOf("Crystal Castle (400 cr)", "Golden Dragon (500 cr)", "Pegasus Wings (300 cr)", "Diamond Tiara (250 cr)"),
            correctOptionIndex = 1,
            category = "Virtual Gifts",
            prizeCredits = 70
        ),
        TriviaQuestion(
            id = "q8",
            question = "ما هو الاسم القديم لشركة جوجل قبل إطلاق اسم Google؟\nWhat was Google's original project name?",
            options = listOf("BackRub", "Googol", "SearchMaster", "WebCrawler"),
            correctOptionIndex = 0,
            category = "Technology",
            prizeCredits = 50
        )
    )

    fun getRandomQuestion(): TriviaQuestion {
        return QUESTIONS.random()
    }
}
