package fr.adrienbrault.idea.symfony2plugin.tests.doctrine;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import fr.adrienbrault.idea.symfony2plugin.tests.SymfonyLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see fr.adrienbrault.idea.symfony2plugin.doctrine.ObjectRepositoryResultTypeProvider
 */
public class ObjectRepositoryResultTypeProviderTest extends SymfonyLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("ObjectRepositoryResultTypeProvider.orm.yml");
        myFixture.copyFileToProject("ObjectRepositoryResultTypeProvider.php");
    }

    public String getTestDataPath() {
        return "src/test/java/fr/adrienbrault/idea/symfony2plugin/tests/doctrine/fixtures";
    }

    public void testThatClassAsStringIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \\Doctrine\\Common\\Persistence\\ObjectManager $om */\n" +
                "$om->getRepository('\\Foo\\Bar')->find('foobar')->get<caret>Id();",
            PlatformPatterns.psiElement(Method.class).withName("getId")
        );
    }

    public void testThatClassAsConstantIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \\Doctrine\\Common\\Persistence\\ObjectManager $om */\n" +
                "$om->getRepository(\\Foo\\Bar::class)->find('foobar')->get<caret>Id();",
            PlatformPatterns.psiElement(Method.class).withName("getId")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \\Doctrine\\Common\\Persistence\\ObjectManager $om */\n" +
                "$om->getRepository(\\Foo\\Bar::class)->findOneBy('foobar')->get<caret>Id();",
            PlatformPatterns.psiElement(Method.class).withName("getId")
        );
    }

    public void testThatArrayAccessIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \\Doctrine\\Common\\Persistence\\ObjectManager $om */\n" +
                "$om->getRepository('\\Foo\\Bar')->findAll()[0]->get<caret>Id();",
            PlatformPatterns.psiElement(Method.class).withName("getId")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \\Doctrine\\Common\\Persistence\\ObjectManager $om */\n" +
                "$om->getRepository('\\Foo\\Bar')->findBy([])[0]->get<caret>Id();",
            PlatformPatterns.psiElement(Method.class).withName("getId")
        );
    }

    public void testThatClassAsStringIsResolvedForMagicMethods() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \\Doctrine\\Common\\Persistence\\ObjectManager $om */\n" +
                "$om->getRepository('\\Foo\\Bar')->findOneByName('foobar')->get<caret>Id();",
            PlatformPatterns.psiElement(Method.class).withName("getId")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \\Doctrine\\Common\\Persistence\\ObjectManager $om */\n" +
                "$om->getRepository('\\Foo\\Bar')->findByName('foobar')[0]->get<caret>Id();",
            PlatformPatterns.psiElement(Method.class).withName("getId")
        );
    }

    public void testThatClassAsStringIsResolvedForMagicMethodsButNotWhenAlreadyExists() {
        // do nothing at all here; use type from the method it self
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \\Doctrine\\Common\\Persistence\\ObjectManager $om */\n" +
                "$om->getRepository('\\Foo\\Bar')->findOneByFancyStuff('foobar')[0]->get<caret>Id();",
            PlatformPatterns.psiElement(Method.class).withName("getId")
        );

        // repository class exists but method is magic
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \\Doctrine\\Common\\Persistence\\ObjectManager $om */\n" +
                "$om->getRepository('\\Foo\\Bar')->findOneByFancyStuffNotMagic('foobar')->get<caret>Id();",
            PlatformPatterns.psiElement(Method.class).withName("getId")
        );
    }
}
