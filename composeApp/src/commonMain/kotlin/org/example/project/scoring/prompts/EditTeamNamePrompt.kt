package org.example.project.scoring.prompts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.scoring.ScoringViewModel.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNamePrompt(
    team: Team,
    teamName: String,
    onNameChange: (Team, String) -> Unit,
    onDismiss: () -> Unit,
) {
    var newName by remember { mutableStateOf(teamName) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray)
            .padding(16.dp)
    ) {
        Column {
            Text("Update ${team.name} Name", fontSize = 18.sp, modifier = Modifier.padding(16.dp))


            TextField(
                value = newName,
                onValueChange = {
                    newName = it
                },
                label = { Text("Team Name") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            Button(
                onClick = {
                    onNameChange(team, newName)
                    onDismiss()
                }
            ) {
                Text("Update Name")
            }
        }
    }
}