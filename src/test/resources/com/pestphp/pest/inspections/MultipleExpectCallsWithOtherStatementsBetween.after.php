<?php

expect(Earth::owner())->toBeInstanceOf(Mouse::class)
    ->and($answer)->toBe(42);
Earth::tryCalculateQuestion();
expect($question)->toBeNull()
    ->and(Earth::exists())->toBeFalse();
