<?php

<caret>expect()->extend('toBeOne', function (): bool {
    return true;
});

expect()->extend('toBeOne', function (): bool {
    return false;
});