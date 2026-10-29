<?php

expect()->extend('toBeOne', function (int $value, string $label = 'one'): bool {
    return true;
});
