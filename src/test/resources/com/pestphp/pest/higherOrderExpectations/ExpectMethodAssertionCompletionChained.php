<?php

class Example {
    public function getDate(): DateTime
    {
        return new DateTime();
    }
}

$example = new Example();

expect($example)->getDate()->getTimestamp()-><caret>