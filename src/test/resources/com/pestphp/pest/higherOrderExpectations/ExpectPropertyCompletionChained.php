<?php

class Chained
{
    public function getTimestamp()
    {
    }
}

class Example {
    public Chained $date;

    public function __construct()
    {
        $this->date = new Chained();
    }
}

$example = new Example();

expect($example)->date-><caret>