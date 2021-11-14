<?php

class BaseTestCase
{
    public function baseTestFunc(){}
}

class FeatureTestCase
{
    public function featureTestFunc(){}
}

class SomeBaseTrait
{
    public function someBaseTraitFunc(){}
}

uses(BaseTestCase::class);

uses(FeatureTestCase::class)->in("Feature");

uses(FeatureTestCase::class, SomeBaseTrait::class)->group("some group")->in("GroupedFeature");

uses(FeatureTestCase::class)->in(__DIR__ . "/DIRFeature");