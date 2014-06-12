/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Francesco Florio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * Francesco Florio
 * Email: floriofrancesco@gmail.com
 * Twitter: @fr4style
 * Linkedin: it.linkedin.com/pub/francesco-florio/21/62/a68/
 *
 * Made with love in the south of Italy (Cosenza, Italy)
 */

package com.fflorio.library.hookedslidingmenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity {

    protected View.OnClickListener clickListener = new View.OnClickListener() {
        @Override public void onClick(View v) {
            final int id = v.getId();
            if(id == R.id.btn1) Toast.makeText(getApplicationContext(), "Btn 1 tapped!", Toast.LENGTH_SHORT).show();
            if(id == R.id.btn2) Toast.makeText(getApplicationContext(), "Btn 2 tapped!", Toast.LENGTH_SHORT).show();
            if(id == R.id.btn3) Toast.makeText(getApplicationContext(), "Btn 3 tapped!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__main);

        final View button1 = findViewById(R.id.btn1);
        button1.setOnClickListener(clickListener);
        final View button2 = findViewById(R.id.btn2);
        button2.setOnClickListener(clickListener);
        final View button3 = findViewById(R.id.btn3);
        button3.setOnClickListener(clickListener);

    }
}