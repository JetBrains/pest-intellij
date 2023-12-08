<?php

<weak_warning descr="Multiple expectations can be chained together">expect(Earth::owner())->toBeInstanceOf(Mouse::class);</weak_warning>
<weak_warning descr="Multiple expectations can be chained together">expect($answer)->toBe(42);</weak_warning>
Earth::tryCalculateQuestion();
<weak_warning descr="Multiple expectations can be chained together">expect($question)->toBeNull();</weak_warning>
<weak_warning descr="Multiple expectations can be chained together">expect(Earth::exists())->toBeFalse();</weak_warning>