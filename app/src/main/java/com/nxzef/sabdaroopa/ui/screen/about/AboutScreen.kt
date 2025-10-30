package com.nxzef.sabdaroopa.ui.screen.about

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nxzef.sabdaroopa.R

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {
    Surface {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            AppHeaderSection()

            Spacer(Modifier.height(64.dp))

            ExpandableSection(
                title = stringResource(R.string.section_story),
                icon = Icons.Default.Face
            ) {
                StorySection()
            }

            Spacer(Modifier.height(16.dp))

            ExpandableSection(
                title = stringResource(R.string.section_features),
                icon = Icons.Default.Star
            ) {
                FeaturesSection()
            }

            Spacer(Modifier.height(16.dp))

            ExpandableSection(
                title = stringResource(R.string.section_built_with),
                icon = Icons.Default.Build
            ) {
                TechnologySection()
            }

            Spacer(Modifier.height(16.dp))

            ExpandableSection(
                title = stringResource(R.string.section_credits),
                icon = Icons.Default.Favorite
            ) {
                CreditsSection()
            }

            Spacer(Modifier.height(16.dp))

            ExpandableSection(
                title = stringResource(R.string.section_developer),
                icon = Icons.Default.AccountCircle
            ) {
                DeveloperSection()
            }

            Spacer(Modifier.height(16.dp))

            ExpandableSection(
                title = stringResource(R.string.section_app_info),
                icon = Icons.Default.Info
            ) {
                AppInfoSection()
            }

            Spacer(Modifier.height(TopAppBarDefaults.TopAppBarExpandedHeight))
        }
    }
}

@Composable
private fun AppHeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.about_app_name),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.about_subtitle),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.about_tagline),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ExpandableSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(
                        if (expanded) R.string.cd_collapse else R.string.cd_expand
                    ),
                    modifier = Modifier.rotate(if (expanded) 180f else 0f)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    HorizontalDivider()
                    Column(modifier = Modifier.padding(16.dp)) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
private fun StorySection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.story_intro),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = stringResource(R.string.story_unique),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = stringResource(R.string.story_journey_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        BulletPoint(stringResource(R.string.story_started))
        BulletPoint(stringResource(R.string.story_duration))
        BulletPoint(stringResource(R.string.story_challenges))
        BulletPoint(stringResource(R.string.story_data_source))

        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.story_conclusion),
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}

@Composable
private fun FeaturesSection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FeatureItem(stringResource(R.string.feature_words))
        FeatureItem(stringResource(R.string.feature_search))
        FeatureItem(stringResource(R.string.feature_offline))
        FeatureItem(stringResource(R.string.feature_quiz))
        FeatureItem(stringResource(R.string.feature_favorites))
        FeatureItem(stringResource(R.string.feature_material))
        FeatureItem(stringResource(R.string.feature_theme))
        FeatureItem(stringResource(R.string.feature_customizable))
    }
}

@Composable
private fun TechnologySection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.tech_intro),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(4.dp))
        TechItem(
            stringResource(R.string.tech_kotlin),
            stringResource(R.string.tech_kotlin_desc)
        )
        TechItem(
            stringResource(R.string.tech_compose),
            stringResource(R.string.tech_compose_desc)
        )
        TechItem(
            stringResource(R.string.tech_room),
            stringResource(R.string.tech_room_desc)
        )
        TechItem(
            stringResource(R.string.tech_hilt),
            stringResource(R.string.tech_hilt_desc)
        )
        TechItem(
            stringResource(R.string.tech_mvvm),
            stringResource(R.string.tech_mvvm_desc)
        )
        TechItem(
            stringResource(R.string.tech_material),
            stringResource(R.string.tech_material_desc)
        )
    }
}

@Composable
private fun CreditsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.credits_guide_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.credits_guide_name),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(R.string.credits_guide_role),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.credits_thanks_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        BulletPoint(stringResource(R.string.credits_varsha))
        BulletPoint(stringResource(R.string.credits_gopi))
        BulletPoint(stringResource(R.string.credits_friends))

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.credits_sources_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        BulletPoint(stringResource(R.string.credits_sabda_manjari))
        BulletPoint(stringResource(R.string.credits_my_coaching))
        BulletPoint(stringResource(R.string.credits_learn_sanskrit))
        BulletPoint(stringResource(R.string.credits_abhyas))
    }
}

@Composable
private fun DeveloperSection() {
    val uriHandler = LocalUriHandler.current

    val emailAddress = stringResource(R.string.developer_email)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.developer_name),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.developer_education),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.developer_bio),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.developer_connect),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        ContactItem(
            icon = Icons.Default.Email,
            text = emailAddress,
            onClick = {
                uriHandler.openUri("mailto:$emailAddress")
            }
        )
        ContactItem(
            icon = Icons.Default.Face,
            text = stringResource(R.string.developer_github),
            onClick = {
                uriHandler.openUri("https://github.com/nxzef")
            }
        )
        ContactItem(
            icon = Icons.Default.AccountCircle,
            text = stringResource(R.string.developer_linkedin),
            onClick = {
                uriHandler.openUri("https://www.linkedin.com/in/nxzef/")
            }
        )
    }
}

@Composable
private fun AppInfoSection() {
    val context = LocalContext.current

    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "Unknown"
        } catch (_: Exception) {
            "Unknown"
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        InfoRow(stringResource(R.string.info_version), versionName)
        InfoRow(stringResource(R.string.info_release), stringResource(R.string.info_release_year))
        InfoRow(stringResource(R.string.info_database), stringResource(R.string.info_database_value))
        InfoRow(stringResource(R.string.info_platform), stringResource(R.string.info_platform_value))
        InfoRow(stringResource(R.string.info_language), stringResource(R.string.info_language_value))

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.info_copyright),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.info_academic),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Helper Composable
@Composable
private fun BulletPoint(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text("•", style = MaterialTheme.typography.bodyMedium)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun FeatureItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun TechItem(name: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ContactItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}