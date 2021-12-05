<?php

class Example {
    public DateTime $date;

    public function __construct()
    {
        $this->date = new DateTime();
    }
}

$example = new Example();

expect($example)->date-><caret>