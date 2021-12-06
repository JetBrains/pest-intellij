<?php

class Chained
{
    public function getTimestamp()
    {
    }
}

class Example
{
    public function getDate(): Chained
    {
        return new Chained();
    }
}

$example = new Example();

expect($example)->getDate()-><caret>