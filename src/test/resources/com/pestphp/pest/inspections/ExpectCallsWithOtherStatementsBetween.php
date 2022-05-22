<?php

expect($answer)->toBe(42);
Earth::tryCalculateQuestion();
expect($question)->toBeNull();
