<?php

class Example {
    public function getTest(): string
    {
        return "works";
    }

    public function getOtherExample(): string
    {
        return "example";
    }
}

$example = new Example();

expect($example)-><caret>