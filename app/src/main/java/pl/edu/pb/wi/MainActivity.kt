package pl.edu.pb.wi

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface


import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.edu.pb.wi.ui.theme.QuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuizScreen();
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart wywołane")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume wywołane")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause wywołane")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop wywołane")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy wywołane")
    }

}


val questionList: Array<Question> = arrayOf(
    Question(R.string.question1, true),
    Question(R.string.question2, false),
    Question(R.string.question3, true),
    Question(R.string.question4, true),
    Question(R.string.question5, true),
)


@Composable
fun ScoreScreen(score: Int, onRestartQuiz: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Twój wynik:",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "$score / ${questionList.size}",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Magenta
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onRestartQuiz() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Zagraj jeszcze raz")
            }
        }
    }
}

@Composable
fun HintDialog(
    hint: String,
    onDismiss: () -> Unit,
    isOpen: Boolean
) {
    if (isOpen) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Podpowiedź") },
            text = { Text(hint) },
            confirmButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Zamknij")
                }
            }
        )
    }
}


@Composable
fun AfterHintDialog(
    hint: String,
    onDismiss: () -> Unit,
    isOpen: Boolean
) {
    if (isOpen) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Sprawdziłeś odpowiedź!") },
            text = { Text(hint) },
            confirmButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Zamknij")
                }
            }
        )
    }
}


@SuppressLint("MutableCollectionMutableState")
@Composable
fun QuizScreen() {
    var currentQuestionIndex by rememberSaveable { mutableStateOf(0) }
    var userAnswer: Boolean? by rememberSaveable { mutableStateOf(null) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var scores by rememberSaveable { mutableStateOf(MutableList(5) { 0 }) }
    var showScoreScreen by rememberSaveable { mutableStateOf(false) }
    var sumScore by rememberSaveable { mutableStateOf(0) }
    var showAnswerDialog by rememberSaveable { mutableStateOf(false) }
    var showHintDialog by rememberSaveable { mutableStateOf(false) }
    var answerWasShown by rememberSaveable { mutableStateOf(false) }
    var tryAnswerQuestion by rememberSaveable { mutableStateOf(false) }


    fun showAnswerDialog() {
        showDialog = true
    }

    fun sumScoreList(): Int {
        if (currentQuestionIndex == 5) {
            for (score in scores) {
                sumScore += score
            }
            currentQuestionIndex++
            return sumScore
        }
        return sumScore
    }

    if (currentQuestionIndex >= questionList.size) {
        ScoreScreen(score = sumScoreList()) {
            currentQuestionIndex = 0
            userAnswer = null
            scores[0] = 0
            scores[1] = 0
            scores[2] = 0
            scores[3] = 0
            scores[4] = 0
            sumScore = 0
            showScoreScreen = false
        }
    } else {
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    currentQuestionIndex++
                    answerWasShown = false
                    userAnswer = null
                    showDialog = false
                },
                title = {
                    Text("Odpowiedz")
                },
                text = {
                    userAnswer?.let {
                        val correctAnswer = questionList[currentQuestionIndex].isTrueAnswer()
                        val answerText = if (correctAnswer) "Prawda" else "Falsz"
                        val userAnswerText = if (it) "Prawda" else "Falsz"

                        if (it == correctAnswer) {
                            scores[currentQuestionIndex] = 1
                        }

                        Text(
                            text = "Odpowiedz $userAnswerText\nPoprawna odpowiedz: $answerText",
                            fontWeight = FontWeight.Bold,
                            color = if (it == correctAnswer) Color.Green else Color.Red
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            currentQuestionIndex++
                            answerWasShown = false
                            userAnswer = null
                        }
                    ) {
                        Text("Następne pytanie")
                    }
                },
                modifier = Modifier.padding(8.dp),
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.tlo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = questionList[currentQuestionIndex].getQuestionId()),
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            userAnswer = true
                            if (!answerWasShown) {
                                showAnswerDialog()
                            }
                            else {
                                tryAnswerQuestion = true
                            }

                        },
                        modifier = Modifier.padding(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color.Green
                        )
                    ) {
                        Text(text = "PRAWDA")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            userAnswer = false
                            if (!answerWasShown) {
                                showAnswerDialog()
                            }
                            else {
                                tryAnswerQuestion = true
                            }
                        },
                        modifier = Modifier.padding(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color.Red
                        )
                    ) {
                        Text(text = "FALSZ")
                    }
                }

                Button(
                    onClick = {
                        answerWasShown = false
                        currentQuestionIndex++
                        answerWasShown = false
                        userAnswer = null
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "NASTEPNE PYTANIE")
                }

                Button(onClick = { showAnswerDialog = true }) {
                    Text("Pokaż odpowiedź")
                }

                if (showAnswerDialog) {
                    AlertDialog(
                        onDismissRequest = { showAnswerDialog = false },
                        title = { Text("Czy na pewno chcesz zobaczyć odpowiedź?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showHintDialog = true
                                    showAnswerDialog = false
                                }
                            ) {
                                Text("Pokaż odpowiedź")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showAnswerDialog = false }
                            ) {
                                Text("Anuluj")
                            }
                        }
                    )
                }


                if (answerWasShown && tryAnswerQuestion) {
                    AfterHintDialog(
                        hint = "Nie możesz odpowiedzieć na to pytanie!",
                        isOpen = true,
                        onDismiss = { tryAnswerQuestion = false }
                    )
                }
                

                if (showHintDialog) {
                    answerWasShown = true;
                    HintDialog(
                        hint = "Odpowiedź: " + if (questionList[currentQuestionIndex].isTrueAnswer()) "Prawda" else "Fałsz",
                        isOpen = true,
                        onDismiss = { showHintDialog = false }
                    )
                }
            }
        }
    }
}




