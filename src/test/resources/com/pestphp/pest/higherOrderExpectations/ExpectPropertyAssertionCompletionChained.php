<?php

class Example {
    public DateTime $test;

    public function __construct()
    {
        $this->test = new DateTime();
    }
}

$example = new Example();

expect($example)->test->getTimestamp()-><caret>