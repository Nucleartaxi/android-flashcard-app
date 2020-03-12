package com.example.alexander.flashcard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    FlashcardDatabase flashcardDatabase;
    List<Flashcard> allFlashcards;

    int currentCardDisplayedIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //switches cards
        findViewById(R.id.flashcard_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);
                findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.flashcard_answer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
            }
        });


        //next button
        findViewById(R.id.next_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // advance our pointer index so we can show the next card
                    currentCardDisplayedIndex++;
                    // make sure we don't get an IndexOutOfBoundsError if we are viewing the last indexed card in our list
                    if (currentCardDisplayedIndex > allFlashcards.size() - 1) {
                        currentCardDisplayedIndex = 0;
                    }
                    // set the question and answer TextViews with data from the database
                    ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                    ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());

                    //makes answer invisible
                    findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                    findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
                }
                catch(Exception e) {
                    ((TextView) findViewById(R.id.flashcard_question)).setText("Input a question!");
                    ((TextView) findViewById(R.id.flashcard_answer)).setText("Input an answer!");
                }

            }
        });

        //adds card
        findViewById(R.id.add_card_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                MainActivity.this.startActivityForResult(intent, 100);
            }
        });

        //delete button
        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardDatabase.deleteCard(((TextView) findViewById(R.id.flashcard_question)).getText().toString());

                //goes back 1 card
                currentCardDisplayedIndex = currentCardDisplayedIndex - 1;
                allFlashcards = flashcardDatabase.getAllCards();

                //if there are no flashcards, display empty state
                if (allFlashcards.size() == 0) {
                    ((TextView) findViewById(R.id.flashcard_question)).setText("Input a question!");
                    ((TextView) findViewById(R.id.flashcard_answer)).setText("Input an answer!");
                }
                //if there are flashcards, display them and switch to a new card
                else {
                    //automatically switch to a new card (all of this next code is same as next button)
                    if (currentCardDisplayedIndex > allFlashcards.size() - 1) {
                        currentCardDisplayedIndex = 0;
                    }
                    ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                    ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());
                    //makes answer invisible
                    findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                    findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
                }
            }
        });


        //reads from database
        flashcardDatabase = new FlashcardDatabase(getApplicationContext());
        allFlashcards = flashcardDatabase.getAllCards();
        Log.d("allFlashcards", allFlashcards.toString());

        if (allFlashcards != null && allFlashcards.size() > 0) {
            ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(0).getQuestion());
            ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(0).getAnswer());
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (data != null) {
                String question = data.getExtras().getString("string1");
                String answer = data.getExtras().getString("string2");

                TextView tv1 = (TextView)findViewById(R.id.flashcard_question);
                tv1.setText(question);
                TextView tv2 = (TextView)findViewById(R.id.flashcard_answer);
                tv2.setText(answer);

                //clears database
                //int i = 0;
                //while (i < allFlashcards.size()) {
                //    String card = allFlashcards.get(i).getQuestion().toString();
                //    Log.d("deleted card", card);
                //    flashcardDatabase.deleteCard(card);
                //    i = i + 1;
                //}

                flashcardDatabase.insertCard(new Flashcard(question, answer));
                allFlashcards = flashcardDatabase.getAllCards();

            }
        }
    }
}
