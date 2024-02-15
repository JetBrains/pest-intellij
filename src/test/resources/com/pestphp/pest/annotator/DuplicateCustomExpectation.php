<?php

<warning descr="Pest custom expectation names must be unique">expect()->extend('toBeOne', function (): bool {
    return true;
})</warning>;

<warning descr="Pest custom expectation names must be unique">expect()->extend('toBeOne', function (): bool {
    return false;
})</warning>;