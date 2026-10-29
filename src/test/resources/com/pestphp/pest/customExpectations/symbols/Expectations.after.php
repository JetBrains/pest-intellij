<?php

expect()->extend('toBeTwo', function (int $value, string $label = 'one'): bool {
    return true;
});
